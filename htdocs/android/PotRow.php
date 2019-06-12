<?php include "./ServerName.php";
  $con = mysqli_connect($server_name, $user_name, $user_password, $db_name, $port);

  $userID = $_GET["userID"];
  if (mysqli_connect_errno())   {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
  }
  $result = mysqli_query($con, "SELECT potCode FROM SMARTPOT WHERE userID = '$userID' ");
  $num = mysqli_num_rows($result);

  echo $num;
  mysqli_close($con);
?>
