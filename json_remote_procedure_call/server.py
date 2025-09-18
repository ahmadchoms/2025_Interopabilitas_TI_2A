from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/rpc', methods=['POST'])
def rpc():
    data = request.get_json()
    if data["method"] == "sample.add":
        result = sum(data["params"])
        return jsonify({"result": result, "error": None, "id": data["id"]})
    if data["method"] == "sample.substract":
        result = data["params"][0] - data["params"][1]
        return jsonify({"result": result, "error": None, "id": data["id"]})
    if data["method"] == "sample.multiply":
        result = data["params"][0] * data["params"][1]
        return jsonify({"result": result, "error": None, "id": data["id"]})
    else:
        return jsonify({"result": None, "error": "Method not found", "id": data["id"]})

if __name__ == '__main__':
    app.run(host='172.16.167.159', port=5000)