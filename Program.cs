using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Newtonsoft.Json;
using RazorPagesPost.Data;
using RedditVault.Models;
var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddRazorPages();
builder.Services.AddDbContext<RazorPagesPostContext>(options =>
    options.UseSqlite(builder.Configuration.GetConnectionString("RazorPagesPostContext") ?? throw new InvalidOperationException("Connection string 'RazorPagesPostContext' not found.")));

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var services = scope.ServiceProvider;

    SeedData.Initialize(services);
}

// Configure the HTTP request pipeline.
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Error");
    // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
    app.UseHsts();
}

app.UseHttpsRedirection();

app.UseRouting();

app.UseAuthorization();

app.MapStaticAssets();
app.MapRazorPages()
   .WithStaticAssets();
var accessToken = "";
var client = new HttpClient();
client.DefaultRequestHeaders.Add("User-Agent","RedditTest/1.0");
var request = new HttpRequestMessage(HttpMethod.Get, "https://oauth.reddit.com/user/33prova33/saved");
request.Headers.Add("Authorization", $"Bearer {accessToken}");
var response = await client.SendAsync(request);

if (response.IsSuccessStatusCode){
    var jsonString = await response.Content.ReadAsStringAsync();
    Console.WriteLine(JsonConvert.DeserializeObject<object>(jsonString));
    Console.WriteLine("bye");   
    // URL of the file to be downloaded
    var fileUrl = "https://i.redd.it/o9sflaranlte1.jpeg";

    // Send a GET request to the specified Uri
    using (var r = await client.GetAsync(fileUrl, HttpCompletionOption.ResponseHeadersRead))
        {
            r.EnsureSuccessStatusCode(); // Throw if not a success code.

            // Path to save the file
            var filePath = Path.Combine(Environment.CurrentDirectory, "mage.jpeg");

            // Read the content into a MemoryStream and then write to file
            using (var ms = await r.Content.ReadAsStreamAsync())
            using (var fs = File.Create(filePath))
            {
                await ms.CopyToAsync(fs);
                fs.Flush();
            }
    }

}
else
{
    Console.WriteLine(response.ToString());
}
        

app.Run();
