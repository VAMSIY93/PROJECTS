<?php
include('phpseclib1/Crypt/RSA.php');
error_reporting(E_ALL);

$service_port = 10000;

$address = getHostByName(getHostName());

$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);

if ($socket === false) 
{
    echo "socket_create() failed: reason: " . socket_strerror(socket_last_error()) . "\n";
} else {
    echo "OK.\n";
}

$result = socket_connect($socket, $address, $service_port);

if ($result === false) {
    echo "socket_connect() failed.\nReason: ($result) " . socket_strerror(socket_last_error($socket)) . "\n";
} else {
    echo "OK.\n";
}

$in = "gmt";
socket_write($socket, $in, strlen($in));

//
$file = "message.txt";

if (file_exists($file)) {
    header('Content-Description: File Transfer');
    header('Content-Type: application/octet-stream');
    header('Content-Disposition: attachment; filename="'.basename($file).'"');
    header('Expires: 0');
    header('Cache-Control: must-revalidate');
    header('Pragma: public');
    header('Content-Length: ' . filesize($file));
    readfile($file);
    exit;
}


//
$signature = "";

    $myFile = fopen("message.txt","r");
    if($myFile)
    {
    	if(($msg = fgets($myFile, 4096))!=false)
    		echo $msg;

    	while(($sign = fgets($myFile, 4096))!=false)
    		$signature = $signature.$sign;
    }
    fclose($myFile);



$len = strlen($msg);
$msg = substr($msg, 0, -1);

$publickey = file_get_contents("public.pem");

    $ok = openssl_verify($msg, $signature, $publickey, OPENSSL_ALGO_SHA256);

    echo "ok:";
    echo $ok;
    echo "\n";

if ($out = socket_read($socket, 2048)) 
{
    if($out==$signature)
    	echo "same";
    echo "\n";
}

echo "Closing socket...";
socket_close($socket);
echo "OK.\n\n";
?>