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
    public class DetailsModel : PageModel
    {
        private readonly RazorPagesPost.Data.RazorPagesPostContext _context;

        public DetailsModel(RazorPagesPost.Data.RazorPagesPostContext context)
        {
            _context = context;
        }

        public Post Post { get; set; } = default!;

        public async Task<IActionResult> OnGetAsync(int? id)
        {
            if (id == null)
            {
                return NotFound();
            }

            var post = await _context.Post.FirstOrDefaultAsync(m => m.Id == id);

            if (post is not null)
            {
                Post = post;

                return Page();
            }

            return NotFound();
        }
    }
}
