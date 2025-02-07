using Microsoft.EntityFrameworkCore;
using RazorPagesPost.Data;

namespace RedditVault.Models;

public static class SeedData
{
    public static void Initialize(IServiceProvider serviceProvider)
    {
        using (var context = new RazorPagesPostContext(
            serviceProvider.GetRequiredService<
                DbContextOptions<RazorPagesPostContext>>()))
        {
            if (context == null || context.Post == null)
            {
                throw new ArgumentNullException("Null RazorPagesPostContext");
            }

            // Look for any posts.
            if (context.Post.Any())
            {
                return;   // DB has been seeded
            }

            context.Post.AddRange(
                new Post
                {
                    Title = "When Harry Met Sally",
                    Date = DateTime.Parse("1989-2-12"),
                },

                new Post
                {
                    Title = "Ghostbusters ",
                    Date = DateTime.Parse("1984-3-13"),
                },

                new Post
                {
                    Title = "Ghostbusters 2",
                    Date = DateTime.Parse("1986-2-23"),
                },

                new Post
                {
                    Title = "Rio Bravo",
                    Date = DateTime.Parse("1959-4-15"),
                }
            );
            context.SaveChanges();
        }
    }
}