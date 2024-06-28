#!/bin/bash

# Check if RabbitMQ is already running
if docker ps | grep -q "rabbitmq"; then
  echo "RabbitMQ is already running"
else
  # Start RabbitMQ container
  docker run -d --hostname rabbitmq --name myrabbit -p 5672:5672 -p 15672:15672 rabbitmq:3.13.3-management
  echo "RabbitMQ started successfully"
fi

mvn clean install && docker compose up --build -d