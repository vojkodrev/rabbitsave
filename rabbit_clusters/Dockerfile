FROM ubuntu

WORKDIR /usr/src/app

RUN apt-get update
RUN apt-get install -y rabbitmq-server

COPY . .

COPY .erlang.cookie /var/lib/rabbitmq/.erlang.cookie
#RUN cat /etc/passwd
RUN chown rabbitmq:rabbitmq /var/lib/rabbitmq/.erlang.cookie
RUN chmod 400 /var/lib/rabbitmq/.erlang.cookie
#RUN ls -al /var/lib/rabbitmq/.erlang.cookie

#RUN ls -al

#RUN ps ax

#RUN rabbitmqctl help

CMD ["./cluster.sh"]
