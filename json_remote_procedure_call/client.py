import requests

url = "http://172.16.167.159:5000/rpc"
payload = {"jsonrpc": "2.0", "method": "sample.add", "params": [5, 3], "id": 1}
payload_multiply = {"jsonrpc": "2.0", "method": "sample.add", "params": [5, 3], "id": 1}
# res = requests.post(url, json=payload)
res = requests.post(url, json=payload_multiply)
print(res.json())