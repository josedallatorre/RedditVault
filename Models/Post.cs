using System.ComponentModel.DataAnnotations;

namespace RedditVault.Models;

public class Post
{
    public int Id { get; set; }
    public string? Title { get; set; }
    [DataType(DataType.Date)]
    public DateTime Date { get; set; }
}