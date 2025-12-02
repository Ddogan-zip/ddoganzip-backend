#!/bin/bash
# EC2 Deployment Script for ddoganzip.site
# Run this script on your EC2 instance

set -e

echo "========================================="
echo "  ddoganzip.site Deployment Script"
echo "========================================="

# Check if .env file exists
if [ ! -f .env ]; then
    echo ""
    echo "ERROR: .env file not found!"
    echo ""
    echo "Create .env file with the following content:"
    echo "----------------------------------------"
    echo "DB_USER=postgres"
    echo "DB_PASSWORD=your-secure-database-password"
    echo "JWT_SECRET=your-256-bit-jwt-secret-key-make-it-very-long-and-random"
    echo "----------------------------------------"
    echo ""
    exit 1
fi

# Check if frontend directory exists
if [ ! -d "./frontend" ]; then
    echo ""
    echo "WARNING: ./frontend directory not found!"
    echo ""
    echo "You need to:"
    echo "1. Build your frontend: npm run build"
    echo "2. Copy the dist folder here: cp -r /path/to/frontend/dist ./frontend"
    echo ""
    read -p "Continue anyway? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
    mkdir -p ./frontend
    echo "<h1>Frontend not deployed yet</h1>" > ./frontend/index.html
fi

echo ""
echo "Step 1: Pulling latest changes..."
git pull origin main || true

echo ""
echo "Step 2: Building and starting containers..."
docker-compose -f docker-compose.prod.yml down || true
docker-compose -f docker-compose.prod.yml up -d --build

echo ""
echo "Step 3: Waiting for services to start..."
sleep 10

echo ""
echo "Step 4: Checking service status..."
docker-compose -f docker-compose.prod.yml ps

echo ""
echo "========================================="
echo "  Deployment Complete!"
echo "========================================="
echo ""
echo "Your site should be available at:"
echo "  https://ddoganzip.site"
echo ""
echo "Backend API:"
echo "  https://ddoganzip.site/api/*"
echo ""
echo "Check logs with:"
echo "  docker-compose -f docker-compose.prod.yml logs -f"
echo ""
