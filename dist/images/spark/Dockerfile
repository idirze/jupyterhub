ARG BASE_CONTAINER=jupyter/scipy-notebook:lab-3.0.16
FROM $BASE_CONTAINER AS all-spark

LABEL maintainer="Jupyter Project <jupyter@googlegroups.com>"

# Fix DL4006
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

USER root

# Spark dependencies
# Default values can be overridden at build time
# (ARGS are in lower case to distinguish them from ENV)
ARG spark_version="3.1.2"
ARG hadoop_version="3.2"
ARG spark_checksum="2385CB772F21B014CE2ABD6B8F5E815721580D6E8BC42A26D70BBCDDA8D303D886A6F12B36D40F6971B5547B70FAE62B5A96146F0421CB93D4E51491308EF5D5"
ARG openjdk_version="8"

ENV APACHE_SPARK_VERSION="${spark_version}" \
    HADOOP_VERSION="${hadoop_version}"

# Kerberos dependencies

RUN apt-get update --yes && \
    apt-get install --yes gcc python-dev libkrb5-dev 

# Openjdk
RUN apt-get update --yes && \
    apt-get install --yes --no-install-recommends \
    "openjdk-${openjdk_version}-jre-headless" \
    ca-certificates-java && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Spark installation
WORKDIR /tmp
RUN wget -q "https://archive.apache.org/dist/spark/spark-${APACHE_SPARK_VERSION}/spark-${APACHE_SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz" && \
    echo "${spark_checksum} *spark-${APACHE_SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz" | sha512sum -c - && \
    tar xzf "spark-${APACHE_SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz" -C /usr/local --owner root --group root --no-same-owner && \
    rm "spark-${APACHE_SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz"

WORKDIR /usr/local

# Configure Spark
ENV SPARK_HOME=/usr/local/spark
ENV SPARK_OPTS="--driver-java-options=-Xms1024M --driver-java-options=-Xmx4096M --driver-java-options=-Dlog4j.logLevel=info" \
    PATH="${PATH}:${SPARK_HOME}/bin"

RUN ln -s "spark-${APACHE_SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}" spark && \
    # Add a link in the before_notebook hook in order to source automatically PYTHONPATH
    mkdir -p /usr/local/bin/before-notebook.d && \
    ln -s "${SPARK_HOME}/sbin/spark-config.sh" /usr/local/bin/before-notebook.d/spark-config.sh

# Fix Spark installation for Java 11 and Apache Arrow library
# see: https://github.com/apache/spark/pull/27356, https://spark.apache.org/docs/latest/#downloading
RUN cp -p "${SPARK_HOME}/conf/spark-defaults.conf.template" "${SPARK_HOME}/conf/spark-defaults.conf" && \
    echo 'spark.driver.extraJavaOptions -Dio.netty.tryReflectionSetAccessible=true' >> "${SPARK_HOME}/conf/spark-defaults.conf" && \
    echo 'spark.executor.extraJavaOptions -Dio.netty.tryReflectionSetAccessible=true' >> "${SPARK_HOME}/conf/spark-defaults.conf"

# Install pyarrow
RUN mamba install --quiet --yes \
    'pyarrow=4.0.*' && \
    mamba clean --all -f -y && \
    fix-permissions "${CONDA_DIR}" && \
    fix-permissions "/home/${NB_USER}"

## Install extensions
RUN  pip install --no-cache-dir  jupyter_contrib_nbextensions \
      && jupyter contrib nbextension install --system \
      && pip install --no-cache-dir jupyter_nbextensions_configurator \
      && jupyter nbextensions_configurator enable --system \
      && pip install --no-cache-dir yapf # for code pretty

COPY extensions.txt /tmp/extensions.txt
RUN pip install --no-cache-dir \
    -r /tmp/extensions.txt

## Spark Kernels
# Install toree: A Jupyter Kernel for Spark2.x/3.x (Scala), Yarn support
RUN pip install --no-cache-dir --upgrade toree \
    && jupyter toree install --spark_home=${SPARK_HOME} --interpreters=Scala,SQL

## Other kernels
# Install sparkmagic kernels: requires Livy
#RUN  pip install sparkmagic --no-cache-dir  \
#     && jupyter-kernelspec install  $(pip3 show sparkmagic | grep Location | cut -d ' ' -f 2)/sparkmagic/kernels/sparkrkernel --user \
#     && jupyter-kernelspec install  $(pip3 show sparkmagic | grep Location | cut -d ' ' -f 2)/sparkmagic/kernels/sparkkernel --user \
#     && jupyter-kernelspec install  $(pip3 show sparkmagic | grep Location | cut -d ' ' -f 2)/sparkmagic/kernels/pysparkkernel --user

# For testing: For testing Released: Mar 23, 2018
#RUN pip install spylon-kernel --no-cache-dir  \
#    && jupyter-kernelspec install  $(pip3 show sparkmagic | grep Location | cut -d ' ' -f 2)/spylon_kernel --user

RUN fix-permissions "${CONDA_DIR}" "/home/${NB_USER}" "${CONDA_DIR}/share/jupyter"

USER ${NB_UID}
WORKDIR "${HOME}"

