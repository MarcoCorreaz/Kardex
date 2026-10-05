import { Link, useLocation } from "react-router-dom";
import { cn } from "@/lib/utils";
import { LayoutDashboard, Package, Tags, ArrowRightLeft, ShoppingCart, LogOut } from "lucide-react";
import { useAuth } from "@/contexts/AuthContext";
import { Button } from "./ui/button";

export function Sidebar() {
  const location = useLocation();
  const { usuario, loja, logout } = useAuth();
  
  const links = [
    { name: "Dashboard", to: "/", icon: LayoutDashboard },
    { name: "Produtos", to: "/produtos", icon: Package },
    { name: "Categorias", to: "/categorias", icon: Tags },
    { name: "Movimentações", to: "/movimentacoes", icon: ArrowRightLeft },
    { name: "PDV Vendas", to: "/vendas", icon: ShoppingCart },
  ];

  return (
    <div className="w-64 bg-card border-r h-screen sticky top-0 flex flex-col">
      <div className="p-6">
        <h1 className="text-2xl font-heading font-bold bg-clip-text text-transparent bg-gradient-to-r from-primary to-blue-600">
          KardexPro
        </h1>
        <p className="text-sm font-semibold text-foreground mt-2">{loja?.nome}</p>
        <p className="text-xs text-muted-foreground">{usuario?.nome}</p>
      </div>
      
      <nav className="flex-1 px-4 space-y-2 mt-2">
        {links.map((link) => {
          const Icon = link.icon;
          const isActive = location.pathname === link.to;
          
          return (
            <Link
              key={link.to}
              to={link.to}
              className={cn(
                "flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-200",
                isActive 
                  ? "bg-primary text-primary-foreground shadow-md" 
                  : "text-muted-foreground hover:bg-muted hover:text-foreground"
              )}
            >
              <Icon className="w-5 h-5" />
              {link.name}
            </Link>
          );
        })}
      </nav>
      
      <div className="p-6 border-t flex flex-col gap-4">
        <Button variant="outline" className="w-full flex items-center gap-2" onClick={logout}>
          <LogOut className="w-4 h-4" />
          Sair
        </Button>
        <div className="text-xs text-muted-foreground text-center">
          <p>KardexPro &copy; 2026</p>
        </div>
      </div>
    </div>
  );
}
