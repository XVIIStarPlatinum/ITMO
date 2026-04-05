FROM python:3.10-slim
RUN mkdir -p /payload
COPY payload.txt /payload/payload.txt
RUN chmod +x /payload/payload.txt
WORKDIR /payload
CMD ["python3", "-m", "http.server", "80"]
