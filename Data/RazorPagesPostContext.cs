using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using RazorPagesMovie.Models;

namespace RazorPagesPost.Data
{
    public class RazorPagesPostContext : DbContext
    {
        public RazorPagesPostContext (DbContextOptions<RazorPagesPostContext> options)
            : base(options)
        {
        }

        public DbSet<RazorPagesMovie.Models.Post> Post { get; set; } = default!;
    }
}
