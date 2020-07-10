FROM node:12
# Create app directory
WORKDIR .
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 5432
CMD [ "node", "eng-registry.js" ]