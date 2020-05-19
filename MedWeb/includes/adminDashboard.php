<h3 class="offBlack" >Add Medication</h3>
<br>
<?php
if(!empty($_SESSION['insertSuccess'])){
		   echo "<center><div class='alert alert-success' role='alert'>". $_SESSION['insertSuccess']. "</div></center>";
	   }		   
	   unset($_SESSION['insertSuccess']);
	   
	   		    if(!empty($_SESSION['insertError'])){
		   echo "<center><div class='alert alert-danger' role='alert'>". $_SESSION['insertError']. "</div></center>";
	   }		   
	   unset($_SESSION['insertError']);
	  
?>
	 <div class='form-group'>
        <form id='attempt_login' method='POST' name='login' action='API/insert.php'>
		<input type='text' name='userID' id='userID' class='form-control primaryGreenBorder' maxlength='128' placeholder='User ID' required><br>
		
		<div class='form-group'>
                <label for='day'>Date:</label>
                <input type='date' class='form-control primaryGreenBorder' name='inputDate' id='inputDate' required>
        </div>
         <div class='form-group'>
                <label for='day'>Time:</label>
                <input type='time' class='form-control primaryGreenBorder' name='inputTime' id='inputTime' required>
        </div>
		
		<label for='pillOneAmount'>Pill One Amount:</label>
		<select type='text' class='form-control primaryGreenBorder' name='pillOneAmount' id='pillOneAmount' required>
			<option value='0'>0</option>
			<option value='1'>1</option>
			<option value='2'>2</option>
			<option value='3'>3</option>
			<option value='4'>4</option>
			<option value='5'>5</option>
		</select>
		
		<label for='pillTwoAmount'>Pill Two Amount:</label>
		<select type='text' class='form-control primaryGreenBorder' name='pillTwoAmount' id='pillTwoAmount' required>
			<option value='0'>0</option>
			<option value='1'>1</option>
			<option value='2'>2</option>
			<option value='3'>3</option>
			<option value='4'>4</option>
			<option value='5'>5</option>
		</select>
		
		<label for='pillThreeAmount'>Pill Three Amount:</label>
		<select type='text' class='form-control primaryGreenBorder' name='pillThreeAmount' id='pillThreeAmount' required>
			<option value='0'>0</option>
			<option value='1'>1</option>
			<option value='2'>2</option>
			<option value='3'>3</option>
			<option value='4'>4</option>
			<option value='5'>5</option>
		</select>
		<br>
         <center><button id='insertButton' name='insertButton' class='btn darkGreenBg offWhite btn-block' id='submit' type='submit' value='Log in'>Submit</button></center>
       </form>
	  </div>
	  <hr>