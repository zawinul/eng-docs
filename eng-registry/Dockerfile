FROM node:12
# Create app directory
WORKDIR /usr/src/app

COPY . .

EXPOSE 5432


#CMD [ "node", "eng-registry.js" ]
CMD ["./start-in-docker.sh"]