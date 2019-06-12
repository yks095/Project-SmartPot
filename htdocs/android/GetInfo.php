<?php include "./ServerName.php";
header('Content-Type: text/html; charset=UTF-8');
$con = mysqli_connect($server_name, $user_name, $user_password, $db_name, $port);

  $name = $_GET["name"];
  $sql="select * from dictionary where name = '$name'";     // SELECT 구문을 통해 DB를 불러옵니다.

  $result=mysqli_query($con,$sql);
  $response = array();

  while ($row = mysqli_fetch_assoc($result)) {
    array_push($response, $row);
  }

  echo json_encode(array("response"=>$response), JSON_UNESCAPED_UNICODE);
  mysqli_close($con);
?>
