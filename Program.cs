using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
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
var request = new HttpRequestMessage(HttpMethod.Get, "https://oauth.reddit.com/api/v1/me");
request.Headers.Add("Authorization", $"Bearer {accessToken}");
var response = await client.SendAsync(request);
response.EnsureSuccessStatusCode();
Console.WriteLine(await response.Content.ReadAsStringAsync());
Console.WriteLine("bye");
app.Run();
