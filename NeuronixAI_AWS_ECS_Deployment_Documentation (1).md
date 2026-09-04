# NeuronixAI --- AWS ECS Deployment Documentation

## 1. Deployment Overview

NeuronixAI backend is a Spring Boot application deployed as a Docker
container on **Amazon ECS using AWS Fargate**.

The goal of this deployment was to get the backend running on AWS,
connect it to the external Neon PostgreSQL database, securely inject
production configuration/secrets, expose the application on port `8080`,
and verify that the application is healthy.

### Current architecture

``` text
Internet
   |
   | HTTP :8080
   v
ECS Task Public IP
   |
   v
Security Group
   |
   v
ECS Fargate Task
neuronix-backend :8080
   |             |
   v             v
Secrets       Neon PostgreSQL
Manager       external DB
```

## 2. AWS Components Used

  Component             Purpose
  --------------------- --------------------------------------------------
  Amazon ECR            Stores the Docker image
  Amazon ECS            Runs and manages containers
  AWS Fargate           Serverless compute for the ECS task
  ECS Task Definition   Container configuration/blueprint
  ECS Service           Maintains the desired task count and deployments
  IAM                   Controls AWS permissions
  AWS Secrets Manager   Stores sensitive configuration
  CloudWatch Logs       Stores application/container logs
  Security Group        Controls network traffic
  Neon PostgreSQL       External PostgreSQL database

## 3. Deployment Flow

``` text
Source Code
    |
    v
Docker Build
    |
    v
Amazon ECR
    |
    v
ECS Task Definition
    |
    v
ECS Service
    |
    v
AWS Fargate
    |
    v
neuronix-backend
```

The application receives production configuration from Secrets Manager
and connects to Neon PostgreSQL.

## 4. Database Configuration

The production database URL used in AWS is:

``` text
jdbc:postgresql://ep-sweet-block-ayyq664g-pooler.c-5.us-east-2.aws.neon.tech/neondb?sslmode=require&channel_binding=require
```

The important requirement is that the Spring datasource URL starts with:

``` text
jdbc:
```

The URL is supplied through the `DB_URL` secret rather than hardcoded
into the application image.

## 5. Secrets Manager

We created the secret:

``` text
neuronix/production
```

It contains values such as:

``` text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
OPENAI_API_KEY
```

The ECS task definition references the secret using `valueFrom`.

Conceptually:

``` text
ECS Task Definition
       |
       v
Secrets Manager
       |
       v
Container environment
```

This prevents passwords and API keys from being baked into the Docker
image or source code.

## 6. IAM Execution Role

The ECS task uses:

``` text
ecsTaskExecutionRole
```

The important additional permission we needed was:

``` text
secretsmanager:GetSecretValue
```

The policy created for the production secret was:

``` json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "ReadNeuronixProductionSecret",
      "Effect": "Allow",
      "Action": "secretsmanager:GetSecretValue",
      "Resource": "arn:aws:secretsmanager:eu-north-1:738063493000:secret:neuronix/production-DGTDIn"
    }
  ]
}
```

The permission was restricted to the Neuronix production secret.

------------------------------------------------------------------------

# 7. Problems We Faced

## Issue 1 --- ECS could not retrieve the secret

### Error

``` text
ResourceInitializationError

unable to retrieve secret from Secrets Manager

AccessDeniedException:
User:
arn:aws:sts::738063493000:assumed-role/ecsTaskExecutionRole/...

is not authorized to perform:
secretsmanager:GetSecretValue

because no identity-based policy allows the
secretsmanager:GetSecretValue action
```

### Root cause

The secret existed, but the ECS task execution role did not have
permission to read it.

### Resolution

We attached the `NeuronixProductionSecretAccess` policy to:

``` text
ecsTaskExecutionRole
```

with:

``` text
secretsmanager:GetSecretValue
```

for the specific secret.

### Lesson

Creating a secret does not automatically give ECS permission to read it.

``` text
Secret exists
      +
IAM permission
      =
ECS can retrieve it
```

------------------------------------------------------------------------

## Issue 2 --- Spring Boot datasource failed

After fixing Secrets Manager access, CloudWatch showed:

``` text
Failed to instantiate HikariDataSource

Factory method 'dataSource' threw exception

url must start with "jdbc"
```

### Root cause

The database URL being supplied to Spring Boot was not being interpreted
as a valid JDBC URL.

### Resolution

We changed `DB_URL` to:

``` text
jdbc:postgresql://ep-sweet-block-ayyq664g-pooler.c-5.us-east-2.aws.neon.tech/neondb?sslmode=require&channel_binding=require
```

Then we created a new ECS task definition revision and redeployed.

### Lesson

Spring Boot/Hikari expects a valid JDBC datasource URL.

------------------------------------------------------------------------

## Issue 3 --- Container exited with code 1

ECS initially showed:

``` text
Essential container in task exited

neuronix-backend
Exit code: 1
```

### Root cause

The application was terminating during startup because of the datasource
configuration problem.

### Resolution

We opened CloudWatch Logs and found the actual Spring Boot exception.

The relevant log group was:

``` text
/ecs/neuronix-task
```

### Lesson

When ECS only shows an exit code, CloudWatch logs are usually the next
place to investigate.

------------------------------------------------------------------------

## Issue 4 --- Application was running but endpoint was unreachable

The ECS task eventually showed:

``` text
Status: Running
```

with:

``` text
Public IP: 13.61.151.72
Port mapping: 8080:8080
```

But the endpoint was initially unreachable.

### Root cause

The Security Group did not allow public inbound TCP traffic on port
`8080`.

The existing inbound rule used the security group itself as the source:

``` text
sg-0177303f6ab668ce9
```

That does not allow arbitrary internet clients to reach port 8080.

### Resolution

We added:

``` text
Type:        Custom TCP
Protocol:    TCP
Port:        8080
Source:      0.0.0.0/0
```

### Lesson

Port mapping alone is not enough.

``` text
Public IP
   |
   v
Security Group
   |
   v
Port 8080
   |
   v
Container
   |
   v
Spring Boot
```

All layers must allow the traffic.

------------------------------------------------------------------------

# 8. CloudWatch Logging

The ECS container uses the AWS Logs driver:

``` text
awslogs
```

Configuration:

``` text
awslogs-create-group  = true
awslogs-group         = /ecs/neuronix-task
awslogs-region        = eu-north-1
awslogs-stream-prefix = ecs
```

Logging flow:

``` text
Spring Boot
    |
    v
Container stdout/stderr
    |
    v
ECS awslogs driver
    |
    v
CloudWatch Logs
```

CloudWatch was essential for finding the datasource startup failure.

------------------------------------------------------------------------

# 9. Final Successful Deployment

After fixing IAM, the JDBC URL, the ECS task definition, and the
Security Group:

``` text
ECS Service
    |
    v
ECS Task
    |
    v
neuronix-backend
    |
    v
Running
```

The backend became reachable through:

``` text
http://13.61.151.72:8080
```

The health endpoint:

``` text
/actuator/health
```

reported:

``` json
{
  "status": "UP"
}
```

This confirmed that the Spring Boot application was running inside ECS
and accepting network traffic.

## Important note

The task public IP can change when ECS replaces the task. Therefore,
this IP should not be considered a permanent production API endpoint.

------------------------------------------------------------------------

# 10. What We Have Learned

This deployment gave us hands-on experience with:

-   Docker containerization
-   Amazon ECR
-   Amazon ECS
-   AWS Fargate
-   ECS Task Definitions
-   ECS Services
-   IAM roles and policies
-   AWS Secrets Manager
-   CloudWatch Logs
-   Security Groups
-   Port mappings
-   Public IP networking
-   Spring Boot production configuration
-   PostgreSQL connectivity
-   ECS troubleshooting

The key lesson is:

``` text
Deployment is not simply Docker -> AWS.
```

It involves several layers:

``` text
Application
    ↓
Docker
    ↓
ECR
    ↓
ECS/Fargate
    ↓
IAM
    ↓
Secrets
    ↓
Database
    ↓
Networking
    ↓
Security Groups
    ↓
Logging
    ↓
Load Balancing
    ↓
Scaling
    ↓
CI/CD
```

------------------------------------------------------------------------

# 11. How Our Deployment Differs From a Big Company's Production Deployment

Our deployment is intentionally simple because the purpose was to learn
the complete deployment chain.

Our current setup is approximately:

``` text
Internet
   |
   v
ECS Task Public IP
   |
   v
Security Group :8080
   |
   v
Spring Boot
```

A mature production architecture would normally look more like:

``` text
                    Internet
                       |
                       v
                  Route 53
                       |
                       v
                Application
                Load Balancer
                   HTTPS
                       |
                       v
                 Private ECS
                    Tasks
                       |
          +------------+------------+
          |            |            |
          v            v            v
       RDS/Aurora    Redis        Kafka
      PostgreSQL    managed      managed
```

## Major differences

  ------------------------------------------------------------------------------
  Area                    Our deployment          Typical production
  ----------------------- ----------------------- ------------------------------
  Entry point             ECS task public IP      Application Load Balancer

  HTTPS                   Not yet                 HTTPS/TLS

  ECS networking          Public task IP          Private ECS tasks

  Database                Neon PostgreSQL         Often RDS/Aurora or another
                                                  managed DB

  Scaling                 Basic ECS service       Auto Scaling

  Deployment              Manual task revision    Automated CI/CD

  Rollback                Manual/controlled       Automated deployment rollback

  Traffic                 Direct to task          ALB distributes traffic

  Security                Basic Security Group    Private subnets, multiple SGs,
                                                  IAM least privilege, WAF

  Secrets                 Secrets Manager         Secrets Manager/SSM + strict
                                                  IAM

  Logs                    CloudWatch              Logs + metrics + tracing +
                                                  alerting

  Availability            Basic                   Multi-AZ/high availability

  Redis                   Not deployed yet        Managed Redis/ElastiCache

  Kafka                   Not deployed yet        Managed Kafka/MSK or
                                                  equivalent

  DNS                     Public IP               Route 53 + domain

  Infrastructure          AWS Console             Terraform/CloudFormation/CDK
                                                  often

  Release strategy        Replace/update service  Blue-green/canary/rolling
                                                  strategies
  ------------------------------------------------------------------------------

------------------------------------------------------------------------

# 12. Why Production Uses an Application Load Balancer

Instead of:

``` text
Internet
   |
   v
One ECS Task
```

production normally uses:

``` text
Internet
   |
   v
ALB
   |
   +----> ECS Task 1
   |
   +----> ECS Task 2
   |
   +----> ECS Task 3
```

Benefits:

-   stable endpoint
-   health checks
-   traffic distribution
-   unhealthy-task removal
-   TLS termination
-   easier scaling
-   no dependency on one task's public IP

If one task fails, the ALB can stop sending traffic to it while healthy
tasks continue serving requests.

------------------------------------------------------------------------

# 13. Why Production ECS Tasks Usually Stay Private

A common architecture is:

``` text
Public Subnet
     |
     v
    ALB
     |
     v
Private Subnet
     |
     +---- ECS Task 1
     +---- ECS Task 2
     +---- ECS Task 3
```

Only the ALB is internet-facing.

This reduces the public attack surface and gives the application tier
stronger network isolation.

------------------------------------------------------------------------

# 14. How Big Companies Deploy New Versions

Our current manual flow is:

``` text
Build image
    |
    v
Push to ECR
    |
    v
Create new task definition revision
    |
    v
Update ECS service
    |
    v
Wait for deployment
```

A mature CI/CD flow is more like:

``` text
Developer
    |
    v
Git Push / Pull Request
    |
    v
CI Pipeline
    |
    +--> Unit Tests
    +--> Integration Tests
    +--> Security Scans
    +--> Docker Build
    +--> Image Scan
    |
    v
ECR
    |
    v
Deployment Pipeline
    |
    v
ECS
    |
    v
ALB
```

The deployment is automated and repeatable instead of requiring manual
console operations.

------------------------------------------------------------------------

# 15. Blue-Green / Canary Deployment

Large systems may avoid immediately sending 100% of production traffic
to a new version.

For example:

``` text
ALB
 |
 +---- Version A: 100%
 |
 +---- Version B:   0%
```

After validation:

``` text
Version A: 90%
Version B: 10%
```

Then:

``` text
Version A: 50%
Version B: 50%
```

Eventually:

``` text
Version A: 0%
Version B: 100%
```

This reduces the risk of deploying a broken version to every user.

------------------------------------------------------------------------

# 16. Where NeuronixAI Is Going Next

The current deployment is the foundation for our next stages.

The architecture can evolve toward:

``` text
                     Frontend
                         |
                         v
                 Route 53 + HTTPS
                         |
                         v
                        ALB
                         |
                         v
                  Private ECS
                   Neuronix API
                         |
             +-----------+-----------+
             |           |           |
             v           v           v
        PostgreSQL     Redis       Kafka
             |           |           |
             +-----------+-----------+
                         |
                         v
                  RAG Processing
                         |
                         v
                    AI / LLMs
```

For our RAG Phase 5 architecture:

### Redis

Potential uses:

-   caching frequently accessed data
-   reducing database/API latency
-   caching retrieval results where appropriate
-   temporary state
-   rate limiting/session-related use cases

### Kafka

Potential uses:

-   asynchronous document ingestion
-   document processing events
-   embedding-generation workflows
-   decoupling background services
-   reliable event-driven processing

These should be introduced when the RAG workflow actually needs them
rather than adding them without a clear responsibility.

------------------------------------------------------------------------

# 17. Current Status

``` text
AWS Region:
eu-north-1 (Stockholm)

Compute:
ECS Fargate

ECS Service:
neuronix-task-service-a5qqus16

Container:
neuronix-backend

Container Port:
8080

Health:
UP

Logging:
CloudWatch

Secrets:
AWS Secrets Manager

Database:
Neon PostgreSQL

Current public endpoint:
http://13.61.151.72:8080

Health endpoint:
/actuator/health
```

------------------------------------------------------------------------

# 18. Final Perspective

The current deployment is **not our final production architecture**.

It is a deliberately simplified deployment that lets us understand the
complete chain:

``` text
Docker
   ↓
ECR
   ↓
ECS
   ↓
Fargate
   ↓
IAM
   ↓
Secrets Manager
   ↓
CloudWatch
   ↓
Security Groups
   ↓
PostgreSQL
```

We will progressively evolve it into:

``` text
Current
ECS Task + Public IP
        ↓
ALB + HTTPS
        ↓
Private ECS Tasks
        ↓
Auto Scaling
        ↓
CI/CD
        ↓
Redis
        ↓
Kafka
        ↓
RAG Pipeline
        ↓
Observability
        ↓
Production-grade NeuronixAI
```

The objective is not merely to make NeuronixAI live. The objective is to
understand **why every infrastructure component exists, what problem it
solves, how the components communicate, and how a simple deployment
evolves into a production-grade distributed system.**
