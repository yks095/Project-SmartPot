<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");

  $userID = $_GET["userID"];

  if (mysqli_connect_errno())   {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }

  $userID = $_GET['userID'];
  $result = mysqli_query($con, "SELECT potName FROM SMARTPOT WHERE userID = '$userID' ");
  $result2 = mysqli_query($con, "SELECT userEmail FROM USER WHERE userID = '$userID' ");

  $response = array();

  $row1 = mysqli_fetch_array($result);
  $row2 = mysqli_fetch_array($result2);

  array_push($response, array("potName"=>$row1[0], "userEmail"=>$row2[0]));
  echo json_encode(array("response"=>$response), JSON_UNESCAPED_UNICODE);

  mysqli_close($con);

?>
