<?php

$msg = gmdate("l jS \of F Y h:i:s A");
$privatekey = file_get_contents("private_unencrypted.pem");

openssl_sign($msg, $signature, $privatekey, OPENSSL_ALGO_SHA256);

$myFile = fopen("message.txt","w") or die("Unable to open file");
fwrite($myFile, $msg."\n");
fwrite($myFile, $signature);
fclose($myFile);

header("Location: client.php");
?>
