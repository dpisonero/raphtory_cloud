# Create folder structure for the different components
# Structure for Spout
# Structure for GraphBuilder
# Structure for PartitionManager
# Structure for Query Manager
# Copy the fatjar and the examples jar to each subfolder for the component


# Configure env variables for each component, we need possibly different ports for connection

# General env variables needed for everyone - same as in Kubernetes part

import os, sys, shutil

docker_image = "docker.io/dpisonero/raphtory:mytag"
jar_name = "./example-lotr_2.13-0.5.jar"
core_jar = "./core-assembly-0.5.jar"
main_class = "com.raphtory.examples.lotrTopic.LOTRService"



os.environ['RAPHTORY_DEPLOY_ID']="kubetory"
os.environ['RAPHTORY_DEPLOY_KUBERNETES_MASTER_URL']="34.175.249.50"

os.environ['RAPHTORY_DEPLOY_KUBERNETES_SECRETS_REGISTRY_SERVER']="registry.docker.com"
os.environ['RAPHTORY_DEPLOY_KUBERNETES_SECRETS_REGISTRY_USERNAME']="dpisonero"
os.environ['RAPHTORY_DEPLOY_KUBERNETES_SECRETS_REGISTRY_PASSWORD']= "X2BR7PhA3l"
os.environ['RAPHTORY_DEPLOY_KUBERNETES_SECRETS_REGISTRY_EMAIL']="david.pisonero.fuentes@alumnos.upm.es"


os.environ['RAPHTORY_DEPLOY_KUBERNETES_DEPLOYMENTS_ALL_PODS_ENV_RAPHTORY_JAVA_RUN_CLASS']="com.raphtory.examples.lotrTopic.LOTRService"
os.environ['RAPHTORY_DEPLOY_KUBERNETES_DEPLOYMENTS_ALL_PODS_ENV_RAPHTORY_PULSAR_BROKER_ADDRESS']="pulsar://pulsar-broker.pulsar.svc.cluster.local:6650"
os.environ['RAPHTORY_DEPLOY_KUBERNETES_DEPLOYMENTS_ALL_PODS_ENV_RAPHTORY_PULSAR_ADMIN_ADDRESS']="http://pulsar-broker.pulsar.svc.cluster.local:8080"
os.environ['RAPHTORY_DEPLOY_KUBERNETES_DEPLOYMENTS_ALL_PODS_ENV_RAPHTORY_ZOOKEEPER_ADDRESS']="pulsar-zookeeper.pulsar.svc.cluster.local:2181"
os.environ['RAPHTORY_DEPLOY_KUBERNETES_DEPLOYMENTS_ALL_PODS_ENV_RAPHTORY_DEPLOY_ID']="kubetory"
os.environ['RAPHTORY_DEPLOY_KUBERNETES_DEPLOYMENTS_ALL_PODS_ENV_RAPHTORY_PARTITIONS_SERVERCOUNT']='1'
os.environ['RAPHTORY_DEPLOY_KUBERNETES_DEPLOYMENTS_ALL_PODS_ENV_RAPHTORY_PARTITIONS_COUNTPERSERVER']='1'
os.environ['RAPHTORY_DEPLOY_KUBERNETES_DEPLOYMENTS_BUILDER_PODS_ENV_RAPHTORY_BUILDERS_COUNTPERSERVER']='1'
os.environ['RAPHTORY_DEPLOY_KUBERNETES_DEPLOYMENTS_SPOUT_PODS_IMAGE']=docker_image
os.environ['RAPHTORY_DEPLOY_KUBERNETES_DEPLOYMENTS_BUILDER_PODS_IMAGE']=docker_image
os.environ['RAPHTORY_DEPLOY_KUBERNETES_DEPLOYMENTS_PARTITIONMANAGER_PODS_IMAGE']=docker_image
os.environ['RAPHTORY_DEPLOY_KUBERNETES_DEPLOYMENTS_QUERYMANAGER_PODS_IMAGE']=docker_image
os.environ['RAPHTORY_DEPLOY_KUBERNETES_DEPLOYMENTS_SPOUT_PODS_REPLICAS']='1'
os.environ['RAPHTORY_DEPLOY_KUBERNETES_DEPLOYMENTS_BUILDER_PODS_REPLICAS']='1'
os.environ['RAPHTORY_DEPLOY_KUBERNETES_DEPLOYMENTS_PARTITIONMANAGER_PODS_REPLICAS']='1'
os.environ['RAPHTORY_DEPLOY_KUBERNETES_DEPLOYMENTS_QUERYMANAGER_PODS_REPLICAS']='1'
os.environ['GOOGLE_APPLICATION_CREDENTIALS']="/raphtory/key.json"


os.environ["RAPHTORY_COMPONENTS_PARTITION_LOG"] = "DEBUG"
os.environ["RAPHTORY_COMPONENTS_QUERYMANAGER_LOG"] = "DEBUG"
os.environ["RAPHTORY_COMPONENTS_QUERYTRACKER_LOG"] = "DEBUG"
os.environ["RAPHTORY_COMPONENTS_SPOUT_LOG"] = "DEBUG"
os.environ["RAPHTORY_COMPONENTS_GRAPHBUILDER_LOG"] = "DEBUG"


# Change the RAM asigned to Java (Scala)
os.environ["JAVA_OPTS"]="-XX:+UseShenandoahGC -XX:+UseStringDeduplication -Xms1G -Xmx1G -Xss128M"

mypath = os.path.abspath(os.path.dirname(__file__))
os.system('java -cp ' + mypath + '/' + core_jar + ':' + mypath + '/' + jar_name + ' ' + main_class)
