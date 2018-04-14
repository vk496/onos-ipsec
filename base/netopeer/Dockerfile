FROM centos:7

MAINTAINER Valiantsin Kivachuk <valiantsin.kivachuk@um.es>

ENV SRC_PATH /usr/src
WORKDIR $SRC_PATH

RUN yum install -y epel-release

RUN yum install -y \
      git \
      make \
      libtool \
      libxml2-devel \
      file \
      libxslt-devel \
      libssh-devel \
      libcurl-devel \
      python-pip \
      libxml2-python \
      openssh-server \
      augeas-devel \
      supervisor \
      nano \
      iproute2 \
      curl \
      git \
      readline-devel \
      bzip2 \
      openssl-devel \
      pam-devel \
      net-tools \
      nc \
      iproute

RUN pip install lxml
RUN ["ssh-keygen", "-A"]
# RUN apt-get update && apt-get install -y --no-install-recommends \
#       supervisor \
#       nano \
#       libreadline-dev \
#       libxml2-dev \
#       python-libxml2 \
#       dh-autoreconf \
#       libtool-bin \
#       libxslt1-dev \
#       libcurl4-openssl-dev \
#       xsltproc \
#       python-setuptools \
#       cmake \
#       zlib1g-dev \
#       libssl-dev \
#       git \
#       pkg-config \
#       ca-certificates \
#       build-essential \
#       python-lxml \
#       ssh \
#       curl \
#       iproute2 \
#       dhcpcd5 \
#       libpam0g-dev \
#     && rm -rf /var/lib/apt/lists/*


#Install pyang
ARG PYANG_VERSION=1.7.4
ENV PYANG_VERSION_BUILD=$PYANG_VERSION

RUN set -e -x; \
    git clone --depth 1 -b pyang-$PYANG_VERSION_BUILD https://github.com/mbj4668/pyang pyang; \
      cd pyang; \
        python setup.py install; \
    rm -Rf $SRC_PATH/pyang

# #Install libssh
# ARG LIBSSH_VERSION=0.7.5
# ENV LIBSSH_VERSION_BUILD=$LIBSSH_VERSION
#
# RUN set -e -x; \
#     git clone -b libssh-$LIBSSH_VERSION_BUILD https://git.libssh.org/projects/libssh.git libssh; \
#       cd libssh; \
#         mkdir build; \
#         cd build; \
#         cmake ..; \
#         make; \
#         make install; \
#     rm -Rf $SRC_PATH/libssh


#Install libnetconf
RUN set -e -x; \
    git clone --depth 1 https://github.com/CESNET/libnetconf libnetconf; \
      cd libnetconf; \
        ./configure --prefix='/usr'; \
        make; \
        make install; \
        ln -s /usr/lib/pkgconfig/libnetconf.pc /usr/lib64/pkgconfig/; \
    rm -Rf $SRC_PATH/libnetconf

#Install netopeer server and client
RUN set -e -x; \
    git clone --depth 1 https://github.com/CESNET/netopeer netopeer; \
      cd netopeer/server; \
        ./configure --prefix='/usr'; \
        make; \
        make install; \
        cp -v config/datastore.xml /usr/etc/netopeer/cfgnetopeer/datastore.xml; \
      cd ../cli; \
        ./configure --prefix='/usr'; \
        make; \
        make install; \
    rm -Rf $SRC_PATH/netopeer


#Install strongswan
ARG STRONGSWAN_VERSION=5.5.0
ENV STRONGSWAN_VERSION_BUILD=$STRONGSWAN_VERSION

RUN set -e -x; \
      curl -O https://download.strongswan.org/strongswan-$STRONGSWAN_VERSION_BUILD.tar.bz2; \
      tar xvf strongswan*; \
      cd strongswan*; \
        ./configure --prefix='/usr' \
            --enable-eap-identity \
            --enable-eap-md5 \
            --enable-eap-mschapv2 \
            --enable-eap-tls \
            --enable-eap-ttls \
            --enable-eap-peap \
            --enable-eap-tnc \
            --enable-eap-dynamic \
            --enable-eap-radius \
            --enable-xauth-eap \
            --enable-xauth-pam \
            --enable-dhcp \
            --enable-openssl \
            --enable-addrblock \
            --enable-unity \
            --enable-certexpire \
            --enable-radattr \
            --enable-swanctl \
            --disable-gmp; \
        make; \
        make install; \
        cp src/libcharon/plugins/vici/libvici.h /usr/local/include/; \
      rm -Rf $SRC_PATH/strongswan*


# #Install netopeer server and client with cfgsystem and cfginterfaces
# RUN git clone --depth 1 https://github.com/CESNET/netopeer netopeer

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
#           sed -i '/<hostname>/d' /usr/etc/netopeer/ietf-system/datastore.xml; \
#     rm -Rf $SRC_PATH/netopeer


COPY conf/netconf-cfg.sh /etc/netconf-cfg.sh
COPY misc/netopeer-server-start /usr/bin/netopeer-server-start
RUN chmod +x /etc/netconf-cfg.sh
COPY misc/supervisord.conf /etc/supervisord.d/supervisord.ini

COPY conf/datastore-acm.xml /var/lib/libnetconf/

CMD /usr/bin/supervisord -c /etc/supervisord.conf

#expose ports
EXPOSE 830
