import urllib.request
from urllib.parse import urljoin, urlencode
import logging

"""
    Send network requests to some url

    For example to send a get request to http://example.com:80/test?code=23
        req = Request("http://example.com", 80)
        print(req.get("test", {"code": 23}))
    
    A string is returned with the contents of the request
"""
class Request:

    def __init__(self, port, host):
        self.port = port
        self.host = host
        self.base = "http://{}:{}/".format(self.port, self.host)

    def get(self, path:str="", params={}) -> str:
        return self._send_get_to_url(self._path_to_url(path), params)

    def _path_to_url(self, path: str):
        return urljoin(self.base, path)

    def _send_get_to_url(self, url, params={}):
        if params:
            full_url = "{}?{}".format(url, urlencode(params))
        else:
            full_url = url

        logging.debug("Sending request to {}".format(full_url))

        with urllib.request.urlopen(full_url) as f:
            return f.read().decode('utf-8')

