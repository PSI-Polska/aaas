FROM quay.io/kskitek/python-base:0.1

EXPOSE 20304

RUN mkdir /engine
WORKDIR /engine
ENV PYTHONPATH /engine

RUN python -m pip install --upgrade matplotlib && \
    python -m pip install --upgrade numpy && \
    python -m pip install --upgrade pandas && \
    python -m pip install --upgrade pytz

COPY src/main/proto/AaasService.proto /engine/
COPY src/main/python/*.py /engine/

RUN python -m grpc_tools.protoc \
    --python_out=. \
    --grpc_python_out=. \
    --proto_path=. \
    AaasService.proto

ENTRYPOINT ["python", "/engine/server.py"]