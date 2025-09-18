const url = "http://172.16.167.159:5000/rpc";
const payload = {
  jsonrpc: "2.0",
  method: "sample.add",
  params: [10, 2],
  id: 3,
};

fetch(url, {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify(payload),
})
  .then((res) => res.json())
  .then((json) => console.log(json))
  .catch((err) => console.error("Error:", err));
