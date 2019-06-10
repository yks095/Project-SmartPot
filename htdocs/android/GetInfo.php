<?php header('Content-Type: text/html; charset=UTF-8');
$con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");

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
