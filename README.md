# Netflix-Clone
This is the core code from scratch to clone the landing page of Netflix using **HTML, CSS and JavaScript.**

## 📸 Screenshots
![image](https://user-images.githubusercontent.com/79099734/156505537-8e28ee14-dd20-4299-9eea-984d7068c7fd.png)
![image](https://user-images.githubusercontent.com/79099734/156505592-42d7e884-e72c-41b8-8efe-856d1aeaf4b1.png)
![image](https://user-images.githubusercontent.com/79099734/156505619-e344eb2f-9298-4f76-8d59-d0f6a4f108dc.png)
![image](https://user-images.githubusercontent.com/79099734/156505658-675daf0b-fe7d-4490-8d1c-ab030527ecf5.png)
![image](https://user-images.githubusercontent.com/79099734/156505698-04ab760c-9ef1-4da2-b921-4c3e65ef0789.png)
![image](https://user-images.githubusercontent.com/79099734/156505771-6929b1f2-1aed-4da4-bb7a-092404589241.png)
![image](https://user-images.githubusercontent.com/79099734/156505809-309a6824-5d85-4cc0-9ffd-95d66fb2cf5e.png)


# Deploy Netflix Clone on Cloud using Jenkins - DevSecOps Project!

### **Phase 1: Initial Setup and Deployment**

**Step 1: Launch EC2 (Ubuntu 22.04):**

- Provision an EC2 instance on AWS with Ubuntu 22.04.
- Connect to the instance using SSH.

**Step 2: Clone the Code:**

- Update all the packages and then clone the code.
- Clone your application's code repository onto the EC2 instance:
    
    ```bash
    git clone https://github.com/P-Sudhakar/Netflix_Clone
    ```

 **Step 3: Install Docker and Run the App Using a Container:**

- Set up Docker on the EC2 instance:
    
    ```bash
    
    sudo apt-get update
    sudo apt-get install docker.io -y
    sudo usermod -aG docker $USER  # Replace with your system's username, e.g., 'ubuntu'
    newgrp docker
    sudo chmod 777 /var/run/docker.sock
    ```
    
- Build and run your application using Docker containers:
    
    ```bash
    docker build -t netflix .
    docker run -d --name netflix -p 8081:80 netflix:latest
    
    #to delete
    docker stop <containerid>
    docker rmi -f netflix
    ```
 **Step 4: CI/CD Setup**

1. **Install Jenkins for Automation:**
    - Install Jenkins on the EC2 instance to automate deployment:
    Install Java
    
    ```bash
    sudo apt update
    sudo apt install fontconfig openjdk-17-jre
    java -version
    openjdk version "17.0.8" 2023-07-18
    OpenJDK Runtime Environment (build 17.0.8+7-Debian-1deb12u1)
    OpenJDK 64-Bit Server VM (build 17.0.8+7-Debian-1deb12u1, mixed mode, sharing)
    
    #jenkins
    sudo wget -O /usr/share/keyrings/jenkins-keyring.asc \
    https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key
    echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
    https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
    /etc/apt/sources.list.d/jenkins.list > /dev/null
    sudo apt-get update
    sudo apt-get install jenkins
    sudo systemctl start jenkins
    sudo systemctl enable jenkins
    ```
    
    - Access Jenkins in a web browser using the public IP of your EC2 instance.
        
        publicIp:8080
**Install Docker Tools and Docker Plugins:**

- Go to "Dashboard" in your Jenkins web interface.
- Navigate to "Manage Jenkins" → "Manage Plugins."
- Click on the "Available" tab and search for "Docker."
- Check the following Docker-related plugins:
  - Docker
  - Docker Commons
  - Docker Pipeline
  - Docker API
  - docker-build-step
- Click on the "Install without restart" button to install these plugins.

**Add DockerHub Credentials:**

- To securely handle DockerHub credentials in your Jenkins pipeline, follow these steps:
  - Go to "Dashboard" → "Manage Jenkins" → "Manage Credentials."
  - Click on "System" and then "Global credentials (unrestricted)."
  - Click on "Add Credentials" on the left side.
  - Choose "Secret text" as the kind of credentials.
  - Enter your DockerHub credentials (Username and Password) and give the credentials an ID (e.g., "docker").
  - Click "OK" to save your DockerHub credentials.

Now, you have installed the Dependency-Check plugin, configured the tool, and added Docker-related plugins along with your DockerHub credentials in Jenkins. You can now proceed with configuring your Jenkins pipeline to include these tools and credentials in your CI/CD process.

```groovy

pipeline {
    agent any
    stages {
        stage('Checkout from Git') {
            steps {
                git 'https://github.com/P-Sudhakar/Netflix_clone'
            }
        }
        stage ('Build a Docker File') {
            steps {
                script{
                   withDockerRegistry(credentialsId: 'docker', toolName: 'docker'){   
                       sh "docker build -t netflix ."
                    }
                }
            }
        } 
        stage ('Tag A DockerImage') {
            steps {
                script{
                   withDockerRegistry(credentialsId: 'docker', toolName: 'docker'){   
                       sh "docker tag netflix sudhakarp1703/netflix:latest "
                   }      
                }               
            }
        }   
         stage ('Push DockerImage to the DockerHub') {
            steps {
                script{
                   withDockerRegistry(credentialsId: 'docker', toolName: 'docker'){   
                       sh "docker push sudhakarp1703/netflix:latest "
                   }      
                }               
            }
        }  
         stage ('Build a Container') {
            steps {
                sh "docker run -itd --name Netflix -p 8081:80 sudhakarp1703/netflix:latest"
            }               
        }
    }
}


If you get docker login failed errorr

sudo su
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins

```

**Step 5: Kubernetes Cluster setup in AWS Environment.

*Prerequisites:
AWS Account: Ensure you have an AWS account.
AWS CLI: Install and configure the AWS CLI.
Install: 
```bash
    pip install awscli
```
Configure: 
```bash
    aws configure
```
kubectl: Install kubectl to interact with the Kubernetes cluster.
Install: 
```bash
    curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
```
Make it executable: 
```bash
    chmod +x ./kubectl
```
Move to PATH: 
```bash
    sudo mv ./kubectl /usr/local/bin/kubectl
```

*Step 1: Create an IAM Role for Worker Nodes
Go to the IAM Console:

Open the IAM Console.
Click on Roles and then Create role.
Create the Role:

Choose EC2 as the trusted entity.
Attach the following policies:
AmazonEKSWorkerNodePolicy
AmazonEC2ContainerRegistryReadOnly
AmazonEKS_CNI_Policy
Name the role (e.g., eksWorkerNodeRole).
Click Create role.

*Step 2: Create an EKS Cluster (If Not Done Already)
Open the EKS Console:
Go to the EKS Console.
Click on Add Cluster and then Create.
Follow the prompts to set up the cluster, using the default settings where appropriate.
Step 3: Add a Managed Node Group
Navigate to Your Cluster:

In the EKS Console, select your cluster.
Click on the Compute tab.
Add a Node Group:

Click on Add Node Group.
Configure the Node Group:

Name: Enter a name for your node group (e.g., my-node-group).
Node IAM Role: Select the IAM role (eksWorkerNodeRole) you created.
AMI Type: Leave as default (Amazon EKS-optimized Amazon Linux 2 AMI).
Instance Types: Choose the default instance type or select a specific type (e.g., t3.medium).
Scaling Configuration:
Set the minimum, desired, and maximum number of nodes (e.g., min: 1, desired: 2, max: 3).
Subnets: Select the subnets where the worker nodes will be deployed.
Remote Access: Optionally, select a key pair for SSH access to the nodes (optional).
Review and Create:

Review your settings.
Click Create to create the node group.


*Step 4: Update kubectl Configuration
To interact with your EKS cluster using kubectl, update your kubeconfig file:
```bash
    aws eks --region us-west-2 update-kubeconfig --name my-cluster
```

*Step 5: Verify Node Group
Check Node Status:
After the node group is created, the worker nodes should automatically join the cluster.
Run the following command to verify:

```bash
    kubectl get nodes
```
