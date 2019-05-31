<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");

  if (mysqli_connect_errno())   {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }

  $userID = $_GET['userID'];
  $result2 = mysqli_query($con, "SELECT flower FROM SMARTPOT WHERE userID = '$userID' ");
  $row2 = mysqli_fetch_array($result2);
  $result = mysqli_query($con, "SELECT lowTemp, highTemp FROM FLOWER WHERE idx = '$row2[0]' ");
  $result3 = mysqli_query($con, "SELECT temperature_sensor FROM SMARTPOT WHERE userID = '$userID' ");
  $response = array();
  $row3 = mysqli_fetch_array($result3);
  while ($row = mysqli_fetch_array($result)) {
    array_push($response, array("lowTemp"=>$row[0], "highTemp"=>$row[1], "tempSensor"=>$row3[0]));
  }
  echo json_encode(array("response"=>$response), JSON_UNESCAPED_UNICODE);
  mysqli_close($con);
?>
