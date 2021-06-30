pip3 install jupyterlab-git==0.30.1


Limits:
=======
- Cache credentials: commit yes, push no
- Commit & push with your own personal token or password

https://discourse.jupyter.org/t/how-does-the-jupyterlab-git-obtain-user-credentials-for-github/9110


export MAVEN_OPTS="-Xmx2048M -Xss128M -XX:MetaspaceSize=512M -XX:MaxMetaspaceSize=2048M -XX:+CMSClassUnloadingEnabled"
./build/mvn -Pyarn -Phadoop-2.7 -Dhadoop.version=2.7.3 -Phive-1.2 -Phive-thriftserver -DskipTests -Dmaven.test.skip=true clean package

./dev/make-distribution.sh --name spark-3.1.2-bin-hadoop2.7-hive1.2.tgz --pip --tgz -Pyarn -Phadoop-2.7 -Phive-1.2 -Phive-thriftserver

# Spark conf
https://towardsdatascience.com/jupyter-notebook-spark-on-kubernetes-880af7e06351
