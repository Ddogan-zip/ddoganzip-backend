#!/bin/bash
# Start ddoganzip backend with H2 database

echo "🚀 Starting ddoganzip backend with H2 database..."
echo "📊 Database: H2 (in-memory)"
echo "🌐 Server: http://localhost:8080"
echo "🗄️  H2 Console: http://localhost:8080/h2-console"
echo ""
echo "Press Ctrl+C to stop"
echo ""

gradle bootRun --args='--spring.profiles.active=h2'
