#!/usr/local/bin/php -q
<?php
include('phpseclib1/Crypt/RSA.php');
error_reporting(E_ALL);

set_time_limit(0);

ob_implicit_flush();

$address = getHostByName(getHostName());
$port = 10000;

if (($sock = socket_create(AF_INET, SOCK_STREAM, SOL_TCP)) === false) 
{
    echo "socket_create() failed: reason: " . socket_strerror(socket_last_error()) . "\n";
}

if (socket_bind($sock, $address, $port) === false) 
{
    echo "socket_bind() failed: reason: " . socket_strerror(socket_last_error($sock)) . "\n";
}

if (socket_listen($sock, 5) === false) 
{
    echo "socket_listen() failed: reason: " . socket_strerror(socket_last_error($sock)) . "\n";
}

//RSA CODE
/*
$rsa = new Crypt_RSA();
extract($rsa->createKey());
$rsa->loadKey($privatekey);

$serRSA = serialize($rsa);
file_put_contents("rsa.txt", $serRSA);

$rsa2 = new Crypt_RSA();
$serRSA2 = file_get_contents("rsa.txt");
$rsa2 = unserialize($serRSA2);

$rsa2->loadKey($privatekey);
echo "private key: \n";
echo $privatekey;
echo "\n";
$msg = gmdate("l jS \of F Y h:i:s A");

$signature = $rsa2->sign($msg);
echo "msg: ".$msg."\n";
echo "sign: \n";
echo $signature;
echo "\n";
echo "len: ";
echo strlen($signature);
echo "\n";


echo "files generated";
echo "\n";

    $rsa3 = new Crypt_RSA();
    $serRSA3 = file_get_contents("rsa.txt");
    $rsa3 = unserialize($serRSA3);
    $rsa3->loadKey($publickey);

    echo "public key: \n";
    echo $publickey;
    echo "\n";

    echo $rsa3->verify($msg, $signature)? 'verified' : 'unverified'; 
*/

$msg = gmdate("l jS \of F Y h:i:s A");
$privatekey = file_get_contents("private_unencrypted.pem");

openssl_sign($msg, $signature, $privatekey, OPENSSL_ALGO_SHA256);

echo "msg: ".$msg."\n";
echo "msglen: ";
echo strlen($msg);
echo "\n";
echo "sign: \n";
echo $signature;
echo "\n";
echo "len: ";
echo strlen($signature);
echo "\n";


$myFile = fopen("message.txt","w") or die("Unable to open file");
fwrite($myFile, $msg."\n");
fwrite($myFile, $signature);
fclose($myFile);

    $publickey = file_get_contents("public.pem");

    $ok = openssl_verify($msg, $signature, $publickey, OPENSSL_ALGO_SHA256);

    echo "ok:";
    echo $ok;
    echo "\n";

    if (($msgsock = socket_accept($sock)) === false) 
    {
        echo "socket_accept() failed: reason: " . socket_strerror(socket_last_error($sock)) . "\n";
        //break;
    }

    do {
        if (false === ($buf = socket_read($msgsock, 2048))) 
        {
            echo "socket_read() failed: reason: " . socket_strerror(socket_last_error($msgsock)) . "\n";
            break 2;
        }
        
        if($buf == 'gmt')
        {
            socket_write($msgsock, $signature, strlen($signature));
            //socket_write($msgsock, $msg, strlen($msg));
        }
    } while (true);

    socket_close($msgsock);
    

socket_close($sock);
?>