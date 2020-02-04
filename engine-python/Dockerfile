FROM python-base:${version}

EXPOSE 20304

RUN mkdir /engine
WORKDIR /engine
ENV PYTHONPATH /engine

COPY src/main/proto/AaasService.proto /engine/
COPY src/main/python/*.py /engine/

RUN python -m grpc_tools.protoc \
    --python_out=. \
    --grpc_python_out=. \
    --proto_path=. \
    AaasService.proto

ENTRYPOINT ["python", "/engine/server.py"]