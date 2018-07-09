from concurrent import futures
import time
import logging

import grpc
import AaasService
import AaasService_pb2_grpc

_ONE_DAY_IN_SECONDS = 60 * 60 * 24
_NUM_OF_WORKERS = 12
_PORT = '20304'
FORMAT = '%(levelname)s %(asctime)-15s %(name)s[%(module)s] [th%(thread)d] - %(message)s'
logging.basicConfig(format=FORMAT)


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=_NUM_OF_WORKERS))
    AaasService_pb2_grpc.add_AAASServicer_to_server(AaasService.AaasService(), server)

    logging.warning('Started server at: %s', _PORT)
    server.add_insecure_port('[::]:' + _PORT)
    server.start()
    try:
        while True:
            time.sleep(_ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        logging.warning('Stopping server due to user interaction')
        server.stop(0)


if __name__ == '__main__':
    serve()
