rm node_modules.tar.gz
tar -h -czvf  node_modules.tar.gz ./node_modules
docker image rm zawinul/eng-registry
docker build -t zawinul/eng-registry .
