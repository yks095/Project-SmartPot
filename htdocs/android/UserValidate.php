<?php include "./ServerName.php";
  $con = mysqli_connect($server_name, $user_name, $user_password, $db_name, $port);
  $userID = $_POST["userID"];

  $statement = mysqli_prepare($con, "SELECT * FROM USER WHERE userID = ?");
  mysqli_stmt_bind_param($statement, "s", $userID);
  mysqli_execute($statement);
  mysqli_stmt_store_result($statement);

  $response = array();
  $response["success"] = true;

  while(mysqli_stmt_fetch($statement)){
    $response["success"] = false;
    $response["userID"] = $userID;
  }

  echo json_encode($response);
?>
