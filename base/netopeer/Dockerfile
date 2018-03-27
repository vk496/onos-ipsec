FROM ubuntu:16.04

MAINTAINER Valiantsin Kivachuk <valiantsin.kivachuk@um.es>

ENV SRC_PATH /usr/src
WORKDIR $SRC_PATH

RUN apt-get update && apt-get install -y --no-install-recommends \
      supervisor \
      nano \
      libreadline-dev \
      libxml2-dev \
      python-libxml2 \
      dh-autoreconf \
      libtool-bin \
      libxslt1-dev \
      libcurl4-openssl-dev \
      xsltproc \
      python-setuptools \
      cmake \
      zlib1g-dev \
      libssl-dev \
      git \
      pkg-config \
      ca-certificates \
      build-essential \
      python-lxml \
      ssh \
      curl \
    && rm -rf /var/lib/apt/lists/*


#Install pyang
ARG PYANG_VERSION=1.7.4
ENV PYANG_VERSION_BUILD=$PYANG_VERSION

RUN set -e -x; \
    git clone --depth 1 -b pyang-$PYANG_VERSION_BUILD https://github.com/mbj4668/pyang pyang; \
      cd pyang; \
        python setup.py install; \
    rm -Rf $SRC_PATH/pyang

#Install libssh
ARG LIBSSH_VERSION=0.7.5
ENV LIBSSH_VERSION_BUILD=$LIBSSH_VERSION

RUN set -e -x; \
    git clone -b libssh-$LIBSSH_VERSION_BUILD https://git.libssh.org/projects/libssh.git libssh; \
      cd libssh; \
        mkdir build; \
        cd build; \
        cmake ..; \
        make; \
        make install; \
    rm -Rf $SRC_PATH/libssh


#Install libnetconf
RUN set -e -x; \
    git clone --depth 1 https://github.com/CESNET/libnetconf libnetconf; \
      cd libnetconf; \
        ./configure; \
        make; \
        make install; \
    rm -Rf $SRC_PATH/libnetconf

#Install netopeer server and client
RUN set -e -x; \
    git clone --depth 1 https://github.com/CESNET/netopeer netopeer; \
      cd netopeer/server; \
        ./configure; \
        make; \
        make install; \
        cp -v config/datastore.xml /usr/local/etc/netopeer/cfgnetopeer/datastore.xml; \
      cd ../cli; \
        ./configure; \
        make; \
        make install; \
    rm -Rf $SRC_PATH/netopeer


RUN apt-get update && apt-get install -y --no-install-recommends \
      iproute2 \
      dhcpcd5 \
    && rm -rf /var/lib/apt/lists/*

# #Install netopeer server and client with cfgsystem and cfginterfaces
# RUN git clone --depth 1 https://github.com/CESNET/netopeer netopeer
#
# RUN set -e -x; \
#     git clone --depth 1 https://github.com/CESNET/netopeer netopeer; \
#       cd netopeer/transAPI; \
#         cd cfginterfaces; \
#           ./configure; \
#           make; \
#           make install; \
#         cd ../cfgsystem; \
#           ./configure; \
#           make; \
#           make install; \
#     rm -Rf $SRC_PATH/netopeer


COPY conf/netconf-cfg.sh /etc/netconf-cfg.sh
COPY conf/netopeer-server-start /usr/bin/netopeer-server-start
RUN chmod +x /etc/netconf-cfg.sh
COPY misc/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# RUN sed -i '/mesg n/d' /root/.profile

CMD /usr/bin/supervisord
