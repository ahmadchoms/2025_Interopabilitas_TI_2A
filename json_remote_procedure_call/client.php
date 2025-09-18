<?php
$url = "http://172.16.167.159:5000/rpc";

$data = array(
    "jsonrpc" => "2.0",
    "method" => "sample.add",
    "params" => array(7, 4),
    "id" => 2
);

$data_subs = array(
    "jsonrpc" => "2.0",
    "method" => "sample.substract",
    "params" => array(7, 4),
    "id" => 2
);

$options = array(
    "http" => array(
        "header" => "Content-Type: application/json\r\n",
        "method" => "POST",
        "content" => json_encode($data_subs)
    )
);

$context = stream_context_create($options);
$result = file_get_contents($url, false, $context);

echo $result;
?>