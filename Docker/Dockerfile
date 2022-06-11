# Base image with Java installed
FROM adoptopenjdk:11

# Copy the project folder into the container
RUN mkdir /app
ADD ./lotr_base /app/lotr_base 

# Install all the packages needed for SDKMAN 
RUN apt update
RUN apt install zip -y
RUN apt install curl -y

# Install SDKMAN (easiest way to install SBT) 
RUN curl -s "https://get.sdkman.io" | bash

# Change into bash shell in order to run a source command
SHELL ["/bin/bash", "-c"]

# Run sdk init scripts and install SBT
RUN source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk install sbt

# Modify PATH from Dockerfile in order to be able to run sbt from docker exec
ENV PATH="/root/.sdkman/candidates/sbt/current/bin:${PATH}"

# Move to project folder
WORKDIR /app/lotr_base