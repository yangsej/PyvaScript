var n = prompt("please input number");
var n = parseInt(n);
var i = 0;
while (i <= n)
{
	if (i == 0)
	{
		var fibo0 = 0;
		var fibo1 = 0;
		var fibo2 = 1;
	}
	else
	{
		fibo1 = fibo0 + fibo2;
		fibo0 = fibo2;
		fibo2 = fibo1;
	}

	i = i + 1;
}

console.log(fibo0);
