FROM python:buster
RUN pip install flask

WORKDIR /opt
COPY ./server.py ./app.py

CMD ["flask", "run", "--host=0.0.0.0"]
