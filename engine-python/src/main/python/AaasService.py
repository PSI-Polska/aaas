import logging
import traceback
from datetime import datetime

import AaasService_pb2
import AaasService_pb2_grpc
import pytz

""" AaaS Service"""


class AaasService(AaasService_pb2_grpc.AAASServicer):
    """AaasService is responsible for handling grpc requests from AaasService.proto definition."""
    __logger = logging.getLogger('AaasService')
    __logger.setLevel(logging.DEBUG)

    def call(self, request, _):
        """Calls function from python module passed in request parameter.
        Returns pb ReturnParameters. In case of error ReturnParameters.errorMessage contains description."""

        func = self.__get_function(request.script)
        if func is None:
            return pb_error_result('Unable to use module: ' + request.script)

        self.__logger.info('Calling %s', request.script)
        try:
            return self.__call_function(func, request)
        except Exception as ex:
            self.__logger.error('When calling %s', request.script)
            traceback.print_exc()
            return pb_error_result(repr(ex))

    def __call_function(self, func, request):
        python_params = map_to_python(request.params)
        func_result = func(python_params)
        self.__logger.debug('Function returned %s', str(func_result))
        return map_to_pb_return(func_result)

    def __get_function(self, script):
        try:
            self.__logger.debug('Loading module %s', script)
            module = __import__(script)
            # should I use importlib.import_module()? wht is the difference?
            func = getattr(module, 'call')
            return func
        except Exception:
            traceback.print_exc()
            return None


def map_to_python(params):
    """"maps pb Parameters to python dict"""
    return {k: __map_vector_to_python(v.vector) for k, v in params.value.items()}


def map_to_pb_return(func_result):
    """maps function result (dict) to pb ReturmParameters"""
    ret_params = AaasService_pb2.ReturnParameters()
    for k, v in func_result.items():
        __map_list_to_pb(ret_params.params.value[k].vector, v)
    return ret_params


def pb_error_result(msg):
    """Prepares pb ReturnParameters with given error message."""
    ret = AaasService_pb2.ReturnParameters()
    ret.errorMessage = msg
    return ret


def __milis_to_dt(ms):
    return datetime.fromtimestamp(ms / 1000.0, tz=pytz.utc)


def __dt_to_milis(dt):
    delta = dt - datetime.fromtimestamp(0, tz=pytz.utc)
    return int(delta.total_seconds() * 1000)


def __map_vector_to_python(vector):
    """maps pb Vector to python list"""
    v = []
    if vector.HasField('doubleValue'):
        v = vector.doubleValue.vector
    elif vector.HasField('longValue'):
        v = vector.longValue.vector
    elif vector.HasField('boolValue'):
        v = vector.boolValue.vector
    elif vector.HasField('stringValue'):
        v = vector.stringValue.vector
    elif vector.HasField('timestampValue'):
        return [__milis_to_dt(__map_value_to_pyton(x)) for x in vector.timestampValue.vector]
    elif vector.HasField('vectorValue'):
        return [__map_vector_to_python(x) for x in vector.vectorValue.vector]
    else:
        raise ValueError('Unsupported type: ' + str(vector))
    return [__map_value_to_pyton(x) for x in v]


def __map_value_to_pyton(v):
    if v.HasField('empty'):
        return None
    else:
        return v.value


def __map_list_to_pb(vector, l):
    """maps python lists to pb Vector"""
    if len(l) == 0:
        return []
    else:
        t = __get_vector_type(l)
    [__map_value_to_pb(vector, t, x) for x in l]


def __get_vector_type(vector):
    """Returns type of elements in collection.
    Raises ValueError when collection is heterogeneous."""
    types = set([x for x in __not_none_types(vector)])
    if len(types) != 1:
        raise ValueError("Heterogeneous collections are not supported", types)
    else:
        return types.pop()


def __not_none_types(list):
    for x in list:
        if x is not None:
            yield type(x)


def __get_bp_value_for_type(type):
    return {
        int: AaasService_pb2.LongValue(),
        float: AaasService_pb2.DoubleValue(),
        bool: AaasService_pb2.BoolValue(),
        str: AaasService_pb2.StringValue(),
        datetime: AaasService_pb2.TimestampValue(),
        list: AaasService_pb2.Vector(),
    }[type]


def __map_value_to_pb(vector, t, value):
    pbv = __get_bp_value_for_type(t)
    if t == int:
        __set_pb_value(pbv, value)
        vector.longValue.vector.extend([pbv])
    elif t == float:
        __set_pb_value(pbv, value)
        vector.doubleValue.vector.extend([pbv])
    elif t == bool:
        __set_pb_value(pbv, value)
        vector.boolValue.vector.extend([pbv])
    elif t == str:
        __set_pb_value(pbv, value)
        vector.stringValue.vector.extend([pbv])
    elif t == datetime:
        if value is None:
            pbv.empty.SetInParent()
        else:
            pbv.value = __dt_to_milis(value)
        vector.timestampValue.vector.extend([pbv])
    elif t == list:
        __map_list_to_pb(pbv, value)
        vector.vectorValue.vector.extend([pbv])


def __set_pb_value(pb_value, value):
    if value is None:
        pb_value.empty.SetInParent()
    else:
        pb_value.value = value
