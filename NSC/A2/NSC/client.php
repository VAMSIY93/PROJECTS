<?php

$in = "gmt";
$file = "message.txt";

$signature = "";

$myFile = fopen("message.txt","r");
if($myFile)
{
    if(($msg = fgets($myFile, 4096))!=false)

    while(($sign = fgets($myFile, 4096))!=false)
        $signature = $signature.$sign;
}
fclose($myFile);

$len = strlen($msg);
$msg = substr($msg, 0, -1);

$publickey = file_get_contents("public.pem");

$ok = openssl_verify($msg, $signature, $publickey, OPENSSL_ALGO_SHA256);

?>
<script type="text/javascript">
  function f1()
  {
    window.location.href = 'gmt.php'; 
  }
</script>
<?php
if($ok == 1)
{
    ?>
    <style>
    body {background-color: LightGrey;}
    a    {color: blue;}
    </style>
    <a href="message.txt" download onclick=f1()><p style="text-align:center">DOWNLOAD</p></a>    
    <script>
        alert("Signature verified..!!")
    </script>
    <?php
}
echo "\n\n\n\nGMT TIMESTAMP: ".$msg;
?>
