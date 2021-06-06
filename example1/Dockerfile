ARG BASE_CONTAINER=python:3.7-alpine
FROM $BASE_CONTAINER

LABEL maintainer="lainotik"

# Install requirenments
COPY requirements.txt requirements.txt
RUN pip3 install -r requirements.txt

# aget user
ARG UID=1000
ARG GID=1000
RUN addgroup -g ${GID} -S agent && adduser -u ${UID} -S agent -G agent

WORKDIR /usr/src/app/agent
COPY ./docker-entrypoint.sh /usr/src/app/agent/
COPY ./src /usr/src/app/agent/src

RUN chmod -R +x /usr/src/app/agent/docker-entrypoint.sh \
    && chown -R agent:nogroup /usr/src/app/agent

USER agent

ENTRYPOINT [ "./docker-entrypoint.sh" ]