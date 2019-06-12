<?php include "./ServerName.php";
$con = mysqli_connect($server_name, $user_name, $user_password, $db_name, $port);
  $sql="select * from dictionary ";     // SELECT 구문을 통해 DB를 불러옵니다.
  $result=mysqli_query($con,$sql);
  $response = array();

  while ($row = mysqli_fetch_array($result)) {
    array_push($response, array("name"=>$row[0], "image"=>$row[1] ));

  }
  echo json_encode(array("response"=>$response), JSON_UNESCAPED_UNICODE);
  mysqli_close($con);
?>
