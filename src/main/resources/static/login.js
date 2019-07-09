// generate a random checkword
function CreateCheckword() {
	// inital for checkword
	var code = '';
	// length of checkword
	var code_length = 4;
	// in order to change "验证" to a random 4bit checkword, whose id was setted as "vertification"/get the element
	var vertification = document.getElementById("vertification");
	// number&letter array
	var letter_set = new Array(0,1,2,3,4,5,6,7,8,9,'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z');
	for (var i=0; i<code_length; i++) {
		var index = Math.floor(Math.random()*36);
		code += letter_set[index];
	}
	vertification.value = code;
	// check if js file work, don't care
	// document.getElementById("demo").innerHTML = "success";
}


function validate() {
	// user input checkword
	var input_checkword = document.getElementById("input_checkword").value.toUpperCase();

}


