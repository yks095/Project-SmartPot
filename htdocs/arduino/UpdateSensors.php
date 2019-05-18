<!-- <?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");

  $potCode = $_POST["id"];
  $sensor1 = $_POST["moisture_sensor"];
  $sensor2 = $_POST["temperature_sensor"];
  $sensor3 = $_POST["cds_sensor"];
  $query = "UPDATE smartpot SET moisture_sensor = (?), temperature_sensor = (?), cds_sensor = (?), update_time=now() WHERE potCode = (?) ";

  $statment = mysqli_prepare($con, $query);
  mysqli_stmt_bind_param($statment, "ssss", $sensor1, $sensor2, $sensor3, $potCode);
  mysqli_stmt_execute($statment);
  $response = array();
  $response["success"] = true;

  echo json_encode($response);

?> -->

<?php
  $con = mysqli_connect("localhost", "root", "smartpot", "arduino", "3307");

  $potCode = $_POST["id"];
  $sensor1 = $_POST["moisture_sensor"];
  $sensor2 = $_POST["temperature_sensor"];
  $sensor3 = $_POST["cds_sensor"];
  $query = "UPDATE smartpot SET moisture_sensor = (?), temperature_sensor = (?), cds_sensor = (?), update_time=now() WHERE potCode = (?) ";

  $statment = mysqli_prepare($con, $query);
  mysqli_stmt_bind_param($statment, "ssss", $sensor1, $sensor2, $sensor3, $potCode);
  mysqli_stmt_execute($statment);

  $response = array();
  $response["success"] = true;

  $var = date("Y-m-d H:i:s",time());
  echo '?'. $var;
  echo json_encode($response);
?>
