var n = prompt("please input number");
var a = parseInt(n);
var i = 1;
var fact = 0;
while (i <= a)
{
	if (i == 1)
	{
		fact = 1;
	}
	else
	{
		fact = fact * i;
	}

	i = i + 1;
}

console.log(fact);
