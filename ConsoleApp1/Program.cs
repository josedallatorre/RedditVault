// See https://aka.ms/new-console-template for more information
/*
Console.WriteLine("What is your name?");
var name = Console.ReadLine();
var currentDate = DateTime.Now;
Console.WriteLine($"{Environment.NewLine}Hello, {name}, on {currentDate:d} at {currentDate:t}!");
Console.Write($"{Environment.NewLine}Press Enter to exit...");
Console.Read();
*/
var accessToken = "";
var client = new HttpClient();
client.DefaultRequestHeaders.Add("User-Agent","RedditTest/1.0");
var request = new HttpRequestMessage(HttpMethod.Get, "https://oauth.reddit.com/api/v1/me");
request.Headers.Add("Authorization", $"Bearer {accessToken}");
var response = await client.SendAsync(request);
response.EnsureSuccessStatusCode();
Console.WriteLine(await response.Content.ReadAsStringAsync());
Console.WriteLine("bye");
