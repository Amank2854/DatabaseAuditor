#!/bin/bash

# Update apt package index
sudo apt-get update

# Install Docker Engine, containerd, and Docker Compose
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Initialize the Postgres, MondoDB, and Neo4j containers
sudo docker compose up -d