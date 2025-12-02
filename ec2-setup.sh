#!/bin/bash
# EC2 Initial Setup Script for Amazon Linux 2023
# Run this script once when you first create your EC2 instance

set -e

echo "========================================="
echo "  EC2 Initial Setup for ddoganzip"
echo "========================================="

echo ""
echo "Step 1: Updating system..."
sudo yum update -y

echo ""
echo "Step 2: Installing Docker..."
sudo yum install -y docker
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER

echo ""
echo "Step 3: Installing Docker Compose..."
DOCKER_COMPOSE_VERSION=$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep '"tag_name":' | sed -E 's/.*"([^"]+)".*/\1/')
sudo curl -L "https://github.com/docker/compose/releases/download/${DOCKER_COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

echo ""
echo "Step 4: Installing Git..."
sudo yum install -y git

echo ""
echo "========================================="
echo "  Setup Complete!"
echo "========================================="
echo ""
echo "IMPORTANT: Log out and log back in for Docker permissions to take effect."
echo ""
echo "Next steps:"
echo "1. Log out: exit"
echo "2. Log back in: ssh ec2-user@your-ec2-ip"
echo "3. Clone repo: git clone https://github.com/Ddogan-zip/ddoganzip-backend.git"
echo "4. cd ddoganzip-backend"
echo "5. Create .env file (see deploy.sh for required variables)"
echo "6. Copy frontend build: scp -r ./dist ec2-user@your-ec2-ip:~/ddoganzip-backend/frontend"
echo "7. Run: ./deploy.sh"
echo ""
