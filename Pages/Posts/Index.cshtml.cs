using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.EntityFrameworkCore;
using RedditVault.Models;
using RazorPagesPost.Data;

namespace RedditVault.Pages.Posts
{
    public class IndexModel : PageModel
    {
        private readonly RazorPagesPost.Data.RazorPagesPostContext _context;

        public IndexModel(RazorPagesPost.Data.RazorPagesPostContext context)
        {
            _context = context;
        }

        public IList<Post> Post { get; set; } = default!;
        [BindProperty(SupportsGet = true)]
        public string? SearchString { get; set; }

        public async Task OnGetAsync()
        {
            var posts = from p in _context.Post
                         select p;
            if (!string.IsNullOrEmpty(SearchString))
            {
                posts = posts.Where(s => s.Title.Contains(SearchString));
            }
            Post = await posts.ToListAsync();
        }
    }
}
