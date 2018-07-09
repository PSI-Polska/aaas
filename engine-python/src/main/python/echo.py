from typing import Any, Dict, List

Params = Dict[str, List[Any]]


def call(params: Params) -> Params:
    print("CalledWith")
    print(params)
    return params
