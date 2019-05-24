<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino","3307");

  if (mysqli_connect_errno())   {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }

  $userID = $_GET['userID'];
  $result = mysqli_query($con, "SELECT moisture_sensor, temperature_sensor FROM SMARTPOT WHERE userID = '$userID' ");

  $response = array();

  while ($row = mysqli_fetch_array($result)) {
    array_push($response, array("sensor"=>$row[0], "tempSensor"=>$row[1]));
  }
  echo json_encode(array("response"=>$response), JSON_UNESCAPED_UNICODE);



  mysqli_close($con);

?>
